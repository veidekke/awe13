class Rule < ActiveRecord::Base
  attr_accessible :name, :status, :priority

  has_one :priority, as: :priorityable, dependent: :destroy
  has_many :conditions, dependent: :destroy
  has_many :activities, dependent: :destroy
end