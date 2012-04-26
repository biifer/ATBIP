module SensorsHelper

	def getMin(o)
		@min = o[0].gw

		o.each do |o| 
			if o.gw < @min 
				@min = o.gw
			end
		end
		return @min
	end

	def getMax(o)
		@max = o[0].gw

		o.each do |o| 
			if o.gw > @max 
				@max = o.gw
			end
		end
		return @max
	end

end
