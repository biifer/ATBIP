class AddGatewayIdToGateway < ActiveRecord::Migration
  def change
    add_column :gateways, :gateway_id, :string
  end
end
