class CreateSensorData < ActiveRecord::Migration
  def change
    create_table :sensor_data do |t|
      t.string :sensor_id
      t.string :value

      t.timestamps
    end
  end
end
