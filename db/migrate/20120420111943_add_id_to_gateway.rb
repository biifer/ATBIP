class AddIdToGateway < ActiveRecord::Migration
  def change
    add_column :gateways, :id, :int
  end
end
