class CreateSensorData < ActiveRecord::Migration
  def change
    create_table :sensor_data do |t|
      t.string :value
      t.string :sensor_id

      t.timestamps
    end
  end
end
