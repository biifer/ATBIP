class AddTypeToSensors < ActiveRecord::Migration
  def change
    add_column :sensors, :type, :string
  end
end
