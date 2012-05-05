class SensorDataNameChangeMigration < ActiveRecord::Migration
  def up
  	create_table :sensor_readings do |t|
      t.string :sensor_id
      t.string :value

      t.timestamps
  	end
  end

  def down
  	drop_table :sensor_data
  end
end
