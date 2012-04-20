class AddNameToSensors < ActiveRecord::Migration
  def change
    add_column :sensors, :name, :string
  end
end
