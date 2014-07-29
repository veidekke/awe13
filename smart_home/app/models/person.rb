class Person < ActiveRecord::Base
  attr_accessible :name, :movement_sensor, :priority, :current_user
  belongs_to :movement_sensor

  has_one :priority, as: :priorityable
end
