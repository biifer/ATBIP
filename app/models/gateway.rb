class Gateway < ActiveRecord::Base
  attr_accessible :gateway_id, :name, :use
  belongs_to :user
  has_many :sensor

end