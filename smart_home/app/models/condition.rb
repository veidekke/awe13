class Condition < ActiveRecord::Base
  attr_accessor :type_filter
  attr_accessible :operator, :value, :resource, :resource_id

  belongs_to :rule
  belongs_to :resource

  validates :resource_id, presence: true
  validates :operator, presence: true
  validates :value, presence: true
  validate :value_has_to_be_in_range

  def value_has_to_be_in_range
    if !resource.nil? && !resource.possible_values.include?(value)
      if resource.is_a?(MovementSensor)
        validate_movement_sensor
      else
        errormsg = "Only the values [" + resource.possible_values.inspect + "] "
        errormsg += "are possible for the type '"+resource.class.to_s+"'"
        errors.add(:value, errormsg)
      end
    end
  end

  private

  def validate_movement_sensor
    if !["husband", "wife", "kid"].include?(value)
      errormsg = "Only the values [" + resource.possible_values.inspect + "] "
      errormsg += "or the values ['husband', 'wife', 'kid'] "
      errormsg += "are possible for the type '"+resource.class.to_s+"'"
      errors.add(:value, errormsg)
    end
    if ["husband", "wife", "kid"].include?(value) && (operator == "<" || operator == ">")
      errormsg = "The Operator '<' or '>' can not be used in the context of 'husband', 'wife' and 'kid'"
      errors.add(:value, errormsg)
    end
  end
end