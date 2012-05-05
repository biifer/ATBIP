class SensorReading < ActiveRecord::Base
  attr_accessible :sensor_id, :value
  belongs_to :sensor, :foreign_key => "sensor_id"
end
