class Ability
	include::Ability
	
	def initialize(user)
		can :read, :all
	end
end