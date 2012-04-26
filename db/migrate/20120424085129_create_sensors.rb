class CreateSensors < ActiveRecord::Migration
  def change
    create_table :sensors do |t|
      t.string :name
      t.string :sensor_type
      t.string :sensor_id

      t.timestamps
    end
  end
end
