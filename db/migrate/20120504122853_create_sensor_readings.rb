class CreateSensorReadings < ActiveRecord::Migration
  def change
    create_table :sensor_readings do |t|
      t.string :sensor_id
      t.string :gateway_id
      t.string :value

      t.timestamps
    end
  end
end
