class Sensor < ActiveRecord::Base
  attr_accessible :sensor_id, :name, :sensor_type, :gateway_id
  belongs_to :gateway
  has_many :sensor_reading
end
