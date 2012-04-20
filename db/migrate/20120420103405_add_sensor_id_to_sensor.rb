class AddSensorIdToSensor < ActiveRecord::Migration
  def change
    add_column :sensors, :sensor_id, :string
  end
end
