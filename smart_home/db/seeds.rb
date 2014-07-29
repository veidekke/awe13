# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
# Examples:
#
#   cities = City.create([{ name: 'Chicago' }, { name: 'Copenhagen' }])
#   Mayor.create(name: 'Emanuel', city: cities.first)

Room.create(name: "Kueche")
Room.create(name: "Bad")
Room.create(name: "Wohnzimmer")
Room.create(name: "Schlafzimmer")
Room.create(name: "Flur")
Room.create(name: "outside")

Actor.all
Sensor.all
led_kitchen = Led.create(name: "licht_kueche", room_id: 1, value: 0, resource_type: "led")
led_bathroom = Led.create(name: "licht_bad", room_id: 2, value: 0, resource_type: "led")
led_livingroom = Led.create(name: "licht_wohnzimmer", room_id: 3, value: 0, resource_type: "led")
led_bedroom = Led.create(name: "licht_schlafzimmer", room_id: 4, value: 0, resource_type: "led")
Led.create(name: "licht_flur", room_id: 5, value: 0, resource_type: "led")
heating_kitchen = Heating.create(name: "heizung_kueche", room_id:1, value: 0, resource_type: "heating")
heating_bath = Heating.create(name: "heizung_bad", room_id:2, value: 0, resource_type: "heating")
heating_living = Heating.create(name: "heizung_wohnzimmer", room_id:3, value: 0, resource_type: "heating")
heating_bedroom = Heating.create(name: "heizung_schlafzimmer", room_id:4, value: 0, resource_type: "heating")

music_kitchen = Music.create(name: "musik_kueche", room_id: 1, value: 0, resource_type: "music")
music_bath = Music.create(name: "musik_bad", room_id: 2, value: 0, resource_type: "music")
music_living = Music.create(name: "musik_wohnzimmer", room_id: 3, value: 0, resource_type: "music")
music_bedroom = Music.create(name: "musik_schlafzimmer", room_id: 4, value: 0, resource_type: "music")

Lock.create(name: "haustuer", room_id: 5, value: 1, resource_type: "lock")

daytime = DayTime.create(name: "tageszeit", room_id: nil, value: 0, resource_type: "daytime")

MovementSensor.create(name: "bewegung_kueche", room_id: 1, value: 0, resource_type: "movement")
MovementSensor.create(name: "bewegung_bad", room_id: 2, value: 0, resource_type: "movement")
mov_living = MovementSensor.create(name: "bewegung_wohnzimmer", room_id: 3, value: 0, resource_type: "movement")
MovementSensor.create(name: "bewegung_schlafzimmer", room_id: 4, value: 0, resource_type: "movement")
MovementSensor.create(name: "bewegung_flur", room_id: 5, value: 0, resource_type: "movement")
bewegung_draussen = MovementSensor.create(name: "bewegung_draussen", room_id: 6, value: 0, resource_type: "movement")



##### Regeln
=begin
rule_kitchen_bath = Rule.create(name: "if_k_on_b_on", priority: Priority.create(), status: "sleeping")
rule_kitchen_bath.conditions << Condition.create(operator: "=", resource: led_kitchen, value: 1)
rule_kitchen_bath.activities << Activity.create(actor: led_bathroom, value: 1)

rule_livingroom_bedroom = Rule.create(name: "if_l_on_b_on", priority: Priority.create(), status: "sleeping")
rule_livingroom_bedroom.conditions << Condition.create(operator: "=", resource: led_livingroom, value: 1)
rule_livingroom_bedroom.activities << Activity.create(actor: led_bedroom, value: 1)
=end

### Szenario: Konflikt zweier Regeln (Wohnzimmersituation)

# All
livingroom_scene_husband = Rule.create(name: "livingroom_scene", priority: Priority.create(), status: "sleeping")
livingroom_scene_husband.conditions << Condition.create(operator: "=", resource: mov_living, value: "1")
livingroom_scene_husband.activities << Activity.create(actor: led_livingroom, value: "1")
livingroom_scene_husband.activities << Activity.create(actor: music_living, value: "0")

# Kid
livingroom_scene_kid = Rule.create(name: "livingroom_scene_kid", priority: Priority.create(), status: "sleeping")
livingroom_scene_kid.conditions << Condition.create(operator: "=", resource: mov_living, value: "kid")
livingroom_scene_kid.activities << Activity.create(actor: led_livingroom, value: "1")
livingroom_scene_kid.activities << Activity.create(actor: music_living, value: "3")
livingroom_scene_kid.activities << Activity.create(actor: heating_living, value: "2")

####

### Szenario: Konflikt Benutzer - Regel
night_scene = Rule.create(name: "night_scene", priority: Priority.create(), status: "sleeping")
night_scene.conditions << Condition.create(operator: "=", resource: daytime, value: "1")
night_scene.activities << Activity.create(actor: music_kitchen, value: "0")
night_scene.activities << Activity.create(actor: music_bath, value: "0")
night_scene.activities << Activity.create(actor: music_bedroom, value: "0")
night_scene.activities << Activity.create(actor: music_living, value: "0")
night_scene.activities << Activity.create(actor: heating_kitchen, value: "0")
night_scene.activities << Activity.create(actor: heating_bath, value: "0")
night_scene.activities << Activity.create(actor: heating_bedroom, value: "0")
night_scene.activities << Activity.create(actor: heating_living, value: "0")


## User
husband = Person.create(name: "husband", movement_sensor: bewegung_draussen, priority: Priority.create(), current_user: true)
wife = Person.create(name: "wife", movement_sensor: bewegung_draussen, priority: Priority.create())
kid = Person.create(name: "kid", movement_sensor: bewegung_draussen, priority: Priority.create())

#rule_kitchen_bath = Rule.create(name: "if_bedroom_on_then_bath_and_livingroom_off", priority: 0, status: "sleeping")
#rule_kitchen_bath.conditions << Condition.create(operator: "=", resource: led_bedroom, value: 1)
#rule_kitchen_bath.activities << Activity.create(actor: led_bathroom, value: 0)
#rule_kitchen_bath.activities << Activity.create(actor: led_livingroom, value: 0)