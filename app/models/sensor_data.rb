class SensorData < ActiveRecord::Base
  attr_accessible :sensor_id, :value
  belongs_to :sensor
end
