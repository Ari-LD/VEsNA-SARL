extends Node3D

const PORTS = [8090, 8095]

var servers : Array[TCPServer] = []
var clients : Array[WebSocketPeer] = []

func _ready() -> void:
	for port in PORTS:
		var server := TCPServer.new()
		if server.listen(port) == OK:
			print("Server listening on port: ", port)
			servers.append(server)
		else:
			push_error("Unable to start the server on port: " + str(port))
			set_process(false)
		
func _process(delta: float) -> void:
	for server in servers:
		while server.is_connection_available():
			var conn : StreamPeerTCP = server.take_connection()
			if conn != null:
				var new_ws := WebSocketPeer.new()
				new_ws.accept_stream(conn)
				clients.append(new_ws)
				print("Nuovo client connesso!")

	for i in range(clients.size() - 1, -1, -1):
		var ws = clients[i]
		ws.poll()
		
		var state = ws.get_ready_state()
		
		if state == WebSocketPeer.STATE_OPEN:
			while ws.get_available_packet_count():
				var msg : String = ws.get_packet().get_string_from_ascii()
				print("Received msg: ", msg)
				var intention = JSON.parse_string(msg)
				if intention is Dictionary:
					manage(intention, ws)
					
		elif state == WebSocketPeer.STATE_CLOSING or state == WebSocketPeer.STATE_CLOSED:
			print("Client disconnected.")
			clients.remove_at(i)

func manage(intention : Dictionary, ws: WebSocketPeer):
	print("manage " + str(intention))
	if intention.has("type") and intention["type"] == "interaction":
		var data = intention["data"]
		if data.has("type") and data["type"] == "make_coffee":
			make_coffee(intention["sender"], data["cup"], ws)

func make_coffee(art_name : String, cup_name : String, ws: WebSocketPeer):
	get_node("CPUParticles3D").visible = true
	await get_tree().create_timer(5.0).timeout
	get_node("CPUParticles3D").visible = false
	
	var log : Dictionary = {}
	log['sender'] = 'artifact'
	log['receiver'] = art_name
	log['type'] = 'signal'
	
	var msg : Dictionary = {}
	msg['type'] = 'interaction'
	msg['status'] = 'completed'
	msg['reason'] = 'coffee_made'
	msg['cup_name'] = cup_name
	log['data'] = msg
	
	if ws.get_ready_state() == WebSocketPeer.STATE_OPEN:
		ws.send_text(JSON.stringify(log))
