class Resource < ActiveRecord::Base
  attr_accessible :name, :resource_type, :room_id, :value, :last_user

  has_and_belongs_to_many :rules
  has_many :conditions
end

