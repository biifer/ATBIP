class AddUserToGateway < ActiveRecord::Migration
  def change
    add_column :gateways, :user, :integer
  end
end
