class CreatePeople < ActiveRecord::Migration
  def change
    create_table :people do |t|
      t.string :name
      t.boolean :current_user, default: false
      t.belongs_to :movement_sensor

      t.timestamps
    end
  end
end
