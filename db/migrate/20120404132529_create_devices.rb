class CreateDevices < ActiveRecord::Migration
  def change
    alter_table :devices do |t|
      t.string :name
      t.integer :id
      t.time :last_changed
      t.text :description
      t.string :address
      t.integer :value

      t.timestamps
    end
  end
end
