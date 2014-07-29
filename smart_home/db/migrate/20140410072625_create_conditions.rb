class CreateConditions < ActiveRecord::Migration
  def change
    create_table :conditions do |t|
      t.string :operator, null: false
      t.belongs_to :rule
      t.belongs_to :resource, null: false
      t.string :value, null: false

      t.timestamps
    end

  end
end
