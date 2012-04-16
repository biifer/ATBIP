class CreateDevices < ActiveRecord::Migration
  def change
    create_table :devices do |t|
      t.string :name
      t.string :uid
      t.text :description
      t.string :address

      t.timestamps
    end
  end
end
