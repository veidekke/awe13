class CreateActivities < ActiveRecord::Migration
  def change
    create_table :activities do |t|
      t.string :value
      t.belongs_to :actor
      t.belongs_to :rule
      t.timestamps
    end
  end
end