class Activity < ActiveRecord::Base
  attr_accessible :value, :actor, :actor_id

  belongs_to :rule
  belongs_to :actor

  validates :actor, presence: true
  validates :value, presence: true
  validate :value_has_to_be_in_range

  def value_has_to_be_in_range
    if !actor.nil? && !actor.possible_values.include?(value)
      errormsg = "Only the values [" + actor.possible_values.inspect + "]"
      errormsg += "are possible for the type '"+actor.class.to_s+"'"
      errors.add(:value, errormsg)
    end
  end
end