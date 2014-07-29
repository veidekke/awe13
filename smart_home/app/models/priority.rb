class Priority < ActiveRecord::Base
  attr_accessor :rule, :person
  belongs_to :priorityable, polymorphic: true

  acts_as_list column: 'value'
end