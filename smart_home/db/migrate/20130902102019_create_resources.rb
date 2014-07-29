class CreateResources < ActiveRecord::Migration
  def change
    create_table :resources do |t|
      t.string :name
      t.integer :room_id
      t.integer :value
      t.string  :type
      t.string :resource_type
      t.string :last_user, default: "Default"

      t.timestamps
    end

    create_table :resources_rules do |t|
      t.belongs_to :resource
      t.belongs_to :rule
    end
  end
end
