class AddOwnerToDevice < ActiveRecord::Migration
  def change
    add_column :devices, :owner, :string
  end
end
