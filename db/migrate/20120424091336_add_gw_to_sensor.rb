class AddGwToSensor < ActiveRecord::Migration
  def change
    add_column :sensors, :gw, :string
  end
end
