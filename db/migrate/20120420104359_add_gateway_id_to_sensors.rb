class AddGatewayIdToSensors < ActiveRecord::Migration
  def change
    add_column :sensors, :gateway_id, :string
  end
end
