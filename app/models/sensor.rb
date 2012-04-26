class Sensor < ActiveRecord::Base
  attr_accessible :name, :sensor_id, :sensor_type, :gw
  belongs_to :gateway
  has_many :sensor_data
end
