class Device < ActiveRecord::Base
  attr_accessible :address, :description, :id, :last_changed, :name, :value
  
  belongs_to :user
end
