class CreatePriorities < ActiveRecord::Migration
  def change
    create_table :priorities do |t|
      t.integer :priorityable_id
      t.string  :priorityable_type
      t.integer :value
      t.timestamps
    end
  end
end