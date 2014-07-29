class SmartHomeController < ApplicationController
  include SmartHomeHelper

  before_filter :set_engine

  def home
    set_current_user_to_default
    @priority_list = Priority.all.sort! { |x,y| x.value <=> y.value }
    RuleEngine.instance.reset

    Rule.all.each do |r|
      RuleEngine.instance.insert_rule(r)
    end
  end

  def sort
    params[:priority].each_with_index do |id, index|
      Priority.update_all(['value=?', index+1], ['id=?', id])
    end

    RuleEngine.instance.timestamp = Time.now()
    RuleEngine.instance.update_priorities

    updated_actors = RuleEngine.instance.get_updated_actor_models
    updated_rules = RuleEngine.instance.get_updated_rule_models
    @priority_list = Priority.all.sort! { |x,y| x.value <=> y.value }

    render :nothing => true
  end

  def info_modal
    render 'smart_home/javascripts/info_modal', formats: [:html, :js]
  end

  def rule_modal
    @rule = Rule.find(params[:id])
 
    render 'smart_home/javascripts/rule_modal', formats: [:html, :js]
  end

  def show_rule
    @priority_list = Priority.all.sort! { |x,y| x.value <=> y.value }
    
    render "smart_home/javascripts/show_rule", formats: [:html, :js], locals: { updated_actors: [], updated_rules: [] }
  end

  def delete_rule
    @rule = Rule.find(params[:id])
    RuleEngine.instance.timestamp = Time.now()
    RuleEngine.instance.delete_rule(@rule)

    @rule.destroy
    updated_actors = RuleEngine.instance.get_updated_actor_models
    updated_rules = RuleEngine.instance.get_updated_rule_models

    @priority_list = Priority.all.sort! { |x,y| x.value <=> y.value }
    render "smart_home/javascripts/show_rule", formats: [:html, :js], locals: { updated_actors: updated_actors.to_a, updated_rules: updated_rules.to_a }
  end

  def new_rule
    @new_condition = Condition.new
    @new_conditions = []
    @current_rule = nil
    
    render "smart_home/javascripts/new_rule", formats: [:html, :js]
  end

  def save_rule
    rule_priority = Priority.create(value: params[:rule].delete(:priority).to_i)

    @rule = Rule.new(params[:rule])
    @rule.priority = rule_priority
    @rule.save
    params[:new_conditions].each { |c| @rule.conditions << Condition.create(c) }
    params[:new_actions].each { |a| @rule.activities << Activity.create(a) }
    RuleEngine.instance.timestamp = Time.now()
    RuleEngine.instance.insert_rule(@rule)
    
    updated_actors = RuleEngine.instance.get_updated_actor_models
    updated_rules = RuleEngine.instance.get_updated_rule_models
    @current_rule = Rule.find(1)
    @priority_list = Priority.all.sort! { |x,y| x.value <=> y.value }

    render "smart_home/javascripts/show_rule", formats: [:html, :js], locals: { updated_actors: updated_actors.to_a, updated_rules: updated_rules.to_a }
  end

  def set_current_person
    update_current_user(params[:person_select])


    render :nothing => true
  end

  def cache_conditions
    @new_conditions = params[:new_conditions] ? params[:new_conditions] : []
    c = Condition.new(params[:condition])
    if c.valid?
      @new_conditions << params[:condition]
    else
      flash[:error] = c.errors.messages.keys.first.to_s + ": " + c.errors.messages.values.first.first
    end
    
    @new_condition = Condition.new
    render "smart_home/javascripts/cache_conditions", formats: [:html, :js]
  end

  def cache_actions
    @new_conditions = params[:new_conditions]
    @new_actions = params[:new_actions] ? params[:new_actions] : []
    
    a = Activity.new(params[:activity])
    if a.valid?
      @new_actions << params[:activity]
    else
      flash[:error] = a.errors.messages.keys.first.to_s + ": " + a.errors.messages.values.first.first
    end

    @new_action = Activity.new
    render "smart_home/javascripts/cache_actions", formats: [:html, :js]
  end

  def new_actions
    @new_action = Activity.new
    @new_conditions = params[:new_conditions]
    @new_actions = []

    render "smart_home/javascripts/cache_actions", formats: [:html, :js]
  end

  def new_rule_name
    @new_rule = Rule.new
    @new_conditions = params[:new_conditions]
    @new_actions = params[:new_actions]

    render "smart_home/javascripts/rule_name", formats: [:html, :js]
  end

  def rule_list
    @priority_list = Priority.all.sort! { |x,y| x.value <=> y.value }

    render "smart_home/javascripts/rule_list", formats: [:html, :js]
  end

  def change_actor
    puts "change_actor: " + params[:value].to_s
    current_actor = Actor.find(params[:id])
    current_user = Person.where(current_user: true).first
    puts "current_user: " + current_user.name
    RuleEngine.instance.timestamp = Time.now()
    RuleEngine.instance.user_interaction(current_user.name, current_actor.name, params[:value].to_i)
    
    render json: { updated_actors: RuleEngine.instance.get_updated_actor_models,
      updated_rules: RuleEngine.instance.get_updated_rule_models
    }
  end

  def change_sensor
    current_sensor = Sensor.find(params[:id])
    RuleEngine.instance.timestamp = Time.now()
    RuleEngine.instance.set_sensor_value(current_sensor.name, params[:value].to_i)

    render json: {updated_actors: RuleEngine.instance.get_updated_actor_models, 
      updated_sensors: Sensor.find(:all),
      updated_rules: RuleEngine.instance.get_updated_rule_models
    }
  end

  def set_room_id
    RuleEngine.instance.timestamp = Time.now()
    RuleEngine.instance.update_room(Person.find(params[:id]), params[:room_id])

    render json: { room_id: params[:room_id],
      updated_actors: RuleEngine.instance.get_updated_actor_models,
      updated_rules: RuleEngine.instance.get_updated_rule_models
    }
  end

  protected

  def set_engine
    @engine = RuleEngine.instance.get_rete
  end
end
