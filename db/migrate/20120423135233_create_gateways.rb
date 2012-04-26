class CreateGateways < ActiveRecord::Migration
  def change
    create_table :gateways do |t|
      t.string :name
      t.string :gateway_id

      t.timestamps
    end
  end
end
