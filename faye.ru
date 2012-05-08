require 'faye'
<<<<<<< HEAD

Faye::WebSocket.load_adapter('thin')

=======
>>>>>>> Added faye
faye_server = Faye::RackAdapter.new(:mount => '/faye', :timeout => 45)
run faye_server