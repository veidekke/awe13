require 'singleton'

class WongiHelper
  MATCHER_MAP = { "=" => 'has',
    "!=" => 'neg',
    "<" => 'less',
    ">" => 'greater'
  }

  def self.getWongiConditions(condition)
    resource = condition.resource
    if(condition.operator == "=" || condition.operator == "!=")
      value = ["husband", "wife", "kid"].include?(condition.value) ? condition.value : condition.value.to_i
      wongi_conditions = [{ method: MATCHER_MAP[condition.operator], parameters: [resource.name, :value, value]}]
    else
      wongi_conditions = []
      wongi_conditions[0] = "has, #{resource.name}, :value, :Value"
      wongi_conditions[1] = "#{MATCHER_MAP[condition.operator]}, :Value, #{condition.value.to_i}"
    end

    wongi_conditions
  end

  def self.getWongiActions(rule, action)
    actions = []
    actions[0] = { method: "gen", parameters: [rule.name, action.actor.name, action.value.to_s] }
    actions[1] = { method: "gen", parameters: [rule.name, :want_action_for, action.actor.name] }

    actions
  end

end

class RuleEngine
	include Singleton

	attr_accessor :wongi_engine
  attr_accessor :timestamp
  attr_accessor :changes
  attr_accessor :old_rule_requests

  USER_PRIORITIES = { "husband" => 0,
                      "wife" => 2,
                      "kid" => 2,
                      "Default" => 0
                    }
  
  def initialize
  	@wongi_engine = Wongi::Engine.create
    init_conflict_management_rules
    add_models_to_engine
  end

  def reset
    @wongi_engine = Wongi::Engine.create
    add_models_to_engine
    init_conflict_management_rules
    simulate_user_interactions
  end

  def simulate_user_interactions
    Actor.all.each do |a|
      if a.last_user != "Default"
        insert_user_request(a.last_user, a.name, a.value)
      end
    end
  end

  def get_rete

  	@wongi_engine
  end

  def insert_rule(rule)
    add_rule_to_engine(rule)
    update_actor_models(get_resources(:actor))
    update_rule_models
  end

  def delete_rule(rule)
    delete_rule_from_engine(rule)
    update_actor_models(get_resources(:actor))
    update_rule_models
  end

  def delete_rule_from_engine(rule)
    @wongi_engine.remove_production(@wongi_engine.productions[rule.name])
    @wongi_engine.retract(@wongi_engine.find rule.name, :priority, :_)
    @wongi_engine.retract(@wongi_engine.find rule.name, :type, :_)
  end

  def add_rule_to_engine(rule)
    @wongi_engine << [rule.name, :priority, rule.priority.value]
    @wongi_engine << [rule.name, :type, :rule]
    wongi_engine = @wongi_engine
    @wongi_engine.rule rule.name do
      forall do
        rule.conditions.each do |c|
          wongi_conditions = WongiHelper.getWongiConditions(c)
          wongi_conditions.each do |wc|
            send(wc[:method], *wc[:parameters])
          end
        end
      end
      make do
        action {
          rule.activities.each do |a|
            wongi_engine.retract( wongi_engine.find(a.actor.name, rule.name, :_) )
            wongi_engine << [a.actor.name, rule.name, Time.now.to_i]
          end
        }
        rule.activities.each do |a|
          wongi_actions = WongiHelper.getWongiActions(self, a)
          wongi_actions.each do |wa|
            send(wa[:method], *wa[:parameters])
          end
        end
      end 
    end
  end

  def update_other_priorities(rule_not_to_update)
    Rule.all.each do |r|
      change_object(r.name, :priority, r.priority.value) unless rule_not_to_update
    end
  end

  def update_priorities
    delete_priorities
    insert_new_priorities
    update_actor_models(get_resources(:actor))
    update_rule_models
  end

  def delete_priorities
    Person.all.each do |p|
      @wongi_engine.retract( @wongi_engine.find p.name, :priority, :_ )
    end
    Rule.all.each do |r|
      @wongi_engine.retract( @wongi_engine.find r.name, :priority, :_ )
    end
  end

  def insert_new_priorities
    Person.all.each { |p| @wongi_engine << [p.name, :priority, p.priority.value] }
    Rule.all.each { |r| @wongi_engine << [r.name, :priority, r.priority.value] }
  end

  def set_sensor_value(sensor_name, value)
    change_object(sensor_name, :value, value)
    
    update_sensor_models(get_resources(:sensor))
    update_actor_models(get_resources(:actor))
    update_rule_models
  end

  def update_room(person, room_id)
    remove_person_from_room(person) unless person.movement_sensor.room_id == 6
    insert_person_in_room(person, room_id)
    update_actor_models(get_resources(:actor))
    update_rule_models
  end

  def user_interaction user, actor_name, value
    insert_user_request(user, actor_name, value)
    update_actor_models(get_resources(:actor))
    update_rule_models
  end

  def cache_rule_requests
    @old_rule_requests = []
    Rule.find(:all).each do |r|
      @old_rule_requests << { rule_name: r.name, rule_requests: @wongi_engine.select(r.name, :want_action_for, :_) }
    end
  end

  def get_rule_status(rule)
    wme_rule_requests = @wongi_engine.select(rule.name, :want_action_for, :_)
    if(wme_rule_requests.empty?)
      rule_status = "sleeping"
    else
      performed_actions = get_performed_actions(rule.activities)
      if(performed_actions == rule.activities)
        rule_status = "active"
      elsif performed_actions.empty?
        rule_status = "waiting"
      else
        rule_status = "active/waiting"
      end
    end
  end

  def get_performed_actions(rule_actions)
    performed_actions = []
    rule_actions.each do |action|
      performed_actions << action if action.actor.value == action.value.to_i
    end
    
    performed_actions
  end

  def update_rule_models
    Rule.find(:all).each do |rule|
      new_status = get_rule_status(rule)
      unless rule.status == new_status
        rule.update_attribute("status", new_status)
        rule.save
      end
    end
  end

  def get_updated_rule_models
    Rule.where(["updated_at > ? ", @timestamp])
  end

  def get_updated_actor_models
    Actor.where(["updated_at > ? ", @timestamp])
  end

  def get_resources(type)
    resources = []
    
    wmes = @wongi_engine.select(:_, :type, type)
    wmes.each do |wme|
      resource_name = wme.subject
      current_value = get_resource_value(resource_name)
      last_user = get_last_user(resource_name)
      resources << {type => get_model(type, resource_name), current_value: current_value, last_user: last_user}
    end

    resources   
  end

  def set_rete wongi_engine

  	@wongi_engine = wongi_engine
  end
  
  def change_object(s, p, o)
    wme = @wongi_engine.find s, p, :_
    @wongi_engine.retract( wme )
    @wongi_engine << [s, p, o]
  end

  protected
  
  def remove_person_from_room(person)
    Person.update(person.id, movement_sensor: nil)
    @wongi_engine.retract( @wongi_engine.find person.movement_sensor.name, :value, person.name)
    if (empty_room?(person.movement_sensor))
      @wongi_engine.retract( @wongi_engine.find person.movement_sensor.name, :value, 1)
      @wongi_engine << [person.movement_sensor.name, :value, 0]
    end
  end

  def insert_person_in_room(person, new_room_id)
    movement_sensor = MovementSensor.where(["room_id = ?", new_room_id]).first
    if empty_room?(movement_sensor)
      @wongi_engine.retract( @wongi_engine.find movement_sensor.name, :value, 0)
      @wongi_engine << [movement_sensor.name, :value, 1]
    end
    Person.update(person.id, movement_sensor: movement_sensor)
    @wongi_engine << [movement_sensor.name, :value, person.name]
  end

  def update_sensor_models(sensors)
    sensors.each do |sensor|
      sensor = Sensor.update(sensor[:sensor].id, value: sensor[:current_value])
      sensor.save
    end
  end

  def update_actor_models(actors)
    @changes = []
    actors.each do |actor|
      actor_model = Actor.find(actor[:actor].id)
      if(actor_model.value != actor[:current_value] || actor_model.last_user != actor[:last_user])
        @changes << { actor: actor_model, last_user_old: actor_model.last_user, last_user_new: actor[:last_user] }
        actor_model.update_attributes(value: actor[:current_value], last_user: actor[:last_user])
        actor_model.save
      end
    end
  end

  def get_model(type, name)
    if(type == :actor)
      model = Actor.where(["name = ?", name]).first
    else
      model = Sensor.where(["name = ?", name]).first
    end

    model
  end

  def empty_room?(movement_sensor)

    movement_sensor.persons.empty?
  end

  def insert_user_request(user, actor_name, value)
    # Zwischenschritt nötig, damit regel aufgrund der retraction nicht erneut gefeuert wird
    # wodurch die regel :fired_at aktualisiert werden würde
    old_time_wme = @wongi_engine.find actor_name, user, :_
    @wongi_engine << [actor_name, user, Time.now.to_i]
    @wongi_engine.retract(old_time_wme)

    @wongi_engine.retract(@wongi_engine.find user, :want_action_for, actor_name)
    @wongi_engine.retract(@wongi_engine.find user, actor_name, :_)

    @wongi_engine << [user, actor_name, value]
    @wongi_engine << [user, :want_action_for, actor_name]
  end

  def init_conflict_management_rules
    init_rule_set_default_value
    init_rule_priority_manager
    init_rule_action_through_highest_Requestor
    init_rule_action_through_latest_Requestor
    init_rule_time_manager
  end

  def init_rule_set_default_value
    @wongi_engine.rule "set_default_value" do
      forall {
        has :Actor, :type, :actor
        ncc {
          has :Actor, :has_a, :last_user
        } 
      }
      make {
        gen :Actor, :value, 0
        gen :Actor, :last_user, "Default"
      }
    end
  end

  def init_rule_priority_manager
    @wongi_engine.rule "priority_manager" do
      forall {
        has :Requestor, :want_action_for, :Actor
        has :Requestor, :Actor, :Value
        has :Requestor, :priority, :Priority

        ncc {
          has :OtherRequestor, :want_action_for, :Actor
          diff :Requestor, :OtherRequestor        
          has :OtherRequestor, :priority, :OtherPriority
          greater :Priority, :OtherPriority
        }
      }
      make {
        # trace values: true, generation: true
        gen :Requestor, :has_highest_priority, :Actor 
      }
    end
  end

  def init_rule_action_through_highest_Requestor
    @wongi_engine.rule "action_through_highest_Requestor" do
      forall {   
        has :HighestRequestor, :has_highest_priority, :Actor
        has :HighestRequestor, :want_action_for, :Actor
        has :HighestRequestor, :Actor, :Value     

        has :LatestRequestor, :is_latest_reqestor_for, :Actor
        has :LatestRequestor, :want_action_for, :Actor
        has :LatestRequestor, :Actor, :Value2
        
        any {
          option {
            has :HighestRequestor, :type, :rule
          }
          option {
            has :HighestRequestor, :type, :user
            same :LatestRequestor, :HighestRequestor
          }
        }
      }
      make {
        action {
          puts "action_through_highest_Requestor"
        }
        gen :Actor, :has_a, :last_user
        gen :Actor, :value, :Value
        gen :Actor, :last_user, :HighestRequestor
      }
    end
  end

  def init_rule_action_through_latest_Requestor
    @wongi_engine.rule "action_through_latest_Requestor" do
      forall {   
        has :HighestRequestor, :has_highest_priority, :Actor
        has :HighestRequestor, :want_action_for, :Actor
        has :HighestRequestor, :Actor, :Value     

        has :LatestRequestor, :is_latest_reqestor_for, :Actor
        has :LatestRequestor, :want_action_for, :Actor
        has :LatestRequestor, :Actor, :Value2
        
        any {
          option {
            has :HighestRequestor, :type, :user
            has :LatestRequestor, :type, :rule
            diff :HighestRequestor, :LatestRequestor
          }
          option {
            has :HighestRequestor, :type, :user
            has :LatestRequestor, :type, :user
            diff :HighestRequestor, :LatestRequestor
          }
        }
      }
      make {
        action {
          puts "action_through_latest_Requestor"
        }
        gen :Actor, :has_a, :last_user
        gen :Actor, :value, :Value2
        gen :Actor, :last_user, :LatestRequestor
      }
    end
  end

  def init_rule_time_manager
    @wongi_engine.rule "time_manager" do
      forall {
        has :Requestor, :want_action_for, :Actor
        has :Requestor, :Actor, :Value
        has :Actor, :Requestor, :Time1

        ncc {
          has :OtherRequestor, :want_action_for, :Actor
          diff :Requestor, :OtherRequestor
          has :Actor, :OtherRequestor, :Time2
          less :Time1, :Time2
        }
      }
      make {
        gen :Requestor, :is_latest_reqestor_for, :Actor 
      }
    end
  end

  def get_resource_value(actor_name)
    value_wme = @wongi_engine.select(actor_name, :value, :_).first
    resource_value = value_wme.nil? ? 0 : value_wme.object
    
    resource_value
  end

  def get_last_user(resource_name)
    last_user_wme = @wongi_engine.select(resource_name, :last_user, :_).first
    last_user = last_user_wme.nil? ? "Default" : last_user_wme.object
    
    last_user
  end

  def add_models_to_engine
    add_actors_to_engine
    add_sensors_to_engine
    add_persons_to_engine
  end

  def add_actors_to_engine
    Actor.find(:all).each do |actor|
      register_actor(actor)
    end
  end

  def add_sensors_to_engine
    Sensor.find(:all).each do |sensor|
      @wongi_engine << [sensor.name, :type, :sensor]
      @wongi_engine << [sensor.name, :value, sensor.value]
      if Sensor.is_a?(MovementSensor)
        Sensor.persons.each { |p| @wongi_engine << [sensor.name, :value, p.name] }
      end
    end
  end

  def add_persons_to_engine
    @wongi_engine << ["Default", :type, :user]
    @wongi_engine << ["Default", :priority, 0]
    Person.find(:all).each do |person|
      @wongi_engine << [person.name, :priority, person.priority.value]
      @wongi_engine << [person.name, :type, :user]
    end
  end

  def is_a_rule?(rule_name)
    index = Rule.find(:all).index { |p| p.name == rule_name }

    !index.nil?
  end

  def register_actor(actor)
    @wongi_engine << [actor.name, :type, :actor]
  end
end