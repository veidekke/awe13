;; JESS Regeln für die 3. Übung
;; Deklaration aller Lichter und Türen im BAALL sowie einfache Regeln als Beispiel

(import jessy.*)
(deftemplate Light       (declare (from-class Light)))
(deftemplate DimLight       (declare (from-class DimLight)))
(deftemplate Door       (declare (from-class Door)))
(deftemplate ChangeDevice (declare (from-class ChangeDevice)))
(deftemplate PlaySound   (declare (from-class PlaySound)))
(deftemplate Telegram    (declare (from-class Telegram)))
(deftemplate Device (slot name) (slot room))
(deftemplate Switch (slot name) (slot room))

;; Devices / rooms
(deffacts data (Device (name "corridorLight") (room "corridor")))
(deffacts data (Device (name "bathroomLight") (room "bathroom")))
(deffacts data (Device (name "bathroomMirrorLight") (room "bathroom")))
(deffacts data (Device (name "kitchenLight") (room "kitchen")))
(deffacts data (Device (name "kitchenJack") (room "kitchen")))
(deffacts data (Device (name "livingLight1") (room "livingroom")))
(deffacts data (Device (name "livingLight2") (room "livingroom")))
(deffacts data (Device (name "livingLight3") (room "livingroom")))
(deffacts data (Device (name "livingJack1") (room "livingroom")))
(deffacts data (Device (name "livingJack2") (room "livingroom")))
(deffacts data (Device (name "bedroomLight1") (room "bedroom")))
(deffacts data (Device (name "bedroomLight2") (room "bedroom")))
(deffacts data (Device (name "bedroomJack1") (room "bedroom")))
(deffacts data (Device (name "bedroomJack2") (room "bedroom")))

(deffacts data (Device (name "lowerLeftDoor") (room "livingroom")))
(deffacts data (Device (name "lowerLeftDoor") (room "corridor")))
(deffacts data (Device (name "upperLeftDoor") (room "livingroom")))
(deffacts data (Device (name "upperLeftDoor") (room "corridor")))
(deffacts data (Device (name "lowerRightDoor") (room "bedroom")))
(deffacts data (Device (name "lowerRightDoor") (room "corridor")))
(deffacts data (Device (name "upperRightDoor") (room "bedroom")))
(deffacts data (Device (name "upperRightDoor") (room "corridor")))
(deffacts data (Device (name "bathroomDoor") (room "bathroom")))
(deffacts data (Device (name "bathroomDoor") (room "corridor")))

(deffacts data (Switch (name "upperRight") (room "bedroom")))
(deffacts data (Switch (name "lowerRight") (room "bedroom")))
(deffacts data (Switch (name "upperLeft") (room "livingroom")))
(deffacts data (Switch (name "lowerLeft") (room "livingroom")))
(deffacts data (Switch (name "bathroom") (room "bathroom")))

;; Add devices with label and groupaddress

;; Lights and DimLights have optional type
(add (new Light "corridorLight" "0/4/1" "main"));
(add (new Light "bathroomLight" "0/4/0" "main"));
(add (new Light "bathroomMirrorLight" "0/4/2" "zone"));
(add (new Light "kitchenLight" "0/0/1" "zone"));
(add (new Light "kitchenJack" "0/0/2"));
(add (new Light "livingLight1" "0/2/0" "zone"));
(add (new Light "livingLight2" "0/2/1" "main"));
(add (new DimLight "livingLight3" "0/2/4" "main"));
(add (new Light "bedroomLight1" "0/1/0" "main"));
(add (new Light "bedroomLight2" "0/1/1" "zone"));
(add (new Light "bedroomJack1" "1/1/0" "zone"));
(add (new Light "bedroomJack2" "1/1/1" "zone"));
(add (new Light "livingJack1" "1/0/0" "zone"));
(add (new Light "livingJack2" "1/0/1"));

;Dieses spezielle Licht simuliert Tageslicht in Yamamoto.
;Momentan keine Instanz im BAALL.
(add (new Light "sunLight" "0/0/0" "sun"));

;; Doors
(add (new Door "lowerLeftDoor" "3/1/1"));
(add (new Door "upperLeftDoor" "3/1/0"));
(add (new Door "lowerRightDoor" "3/0/1"));
(add (new Door "upperRightDoor" "3/0/0"));
(add (new Door "bathroomDoor" "3/2/0"));

(reset)

(defglobal ?*last_room* = "livingroom")

;; Now define the rules.

/*
(defrule uebung03aufg31
    "Wenn per Schalter in einem Raum eine beliebige Tür
    geschlossen wird, mache alle Lichter in anderen Räumen
    aus."
	(Telegram {value == 0} (dest ?dest) (source ?src))     ; geschlossen
	(Door {label == ?dest})                                ; Tür wurde geschaltet
	(Switch {name == ?src} (room ?srcroom))                ; Quellraum des Schalters
	(Device (name ?dev_in_other_room) {room != ?srcroom})  ; Geräte nicht im Quellraum
	?light <- (or (Light {label == ?dev_in_other_room}) (DimLight {label == ?dev_in_other_room})) ; Lichter/Dimmlichter in anderen Räumen
    =>
	(add (new ChangeDevice ?light.label 0))  ; Lichter/Dimmlichter ausschalten
  (printout t "Aufgabe 3.1" crlf))
  
  
(defrule uebung03aufg32
    "Wird in einem Raum ein Lichtschalter gedrückt, gehen in
     allen anderen Räumen die Lichter aus (außer das gerade eingeschaltete)."
  (Telegram (dest ?dest) (source ?src))
  (Switch {name == ?src} (room ?srcroom))                     ; Quellraum des Schalters
  (or (Light {label == ?dest}) (DimLight {label == ?dest}))   ; Licht/Dimmlicht wurde geschaltet
  (Device (name ?dev_in_other_room) {room != ?srcroom})       ; Geräte nicht im Quellraum
	?light <- (or
                (Light {label == ?dev_in_other_room && label != ?dest})
                (DimLight {label == ?dev_in_other_room && label != ?dest})
            ) ; Lichter/Dimmlichter in anderen Räumen, die gerade nicht geschaltet wurden
    =>
	(add (new ChangeDevice ?light.label 0))  ; Lichter/Dimmlichter ausschalten
  (printout t "Aufgabe 3.2" crlf))
  
  
(defrule uebung03aufg33
    "Wenn eine Tür geöffnet wird und im Raum, wo der Schalter
     gedrückt wurde, ein beliebiges Licht brennt, dann schalte im
     Nachbarraum das Deckenlicht an.."
  (Telegram {value == 1} (dest ?dest) (source ?src))  ; geöffnet
  (Door {label == ?dest})                             ; nur wenn eine Tür geschaltet wird
  
  (Switch {name == ?src} (room ?srcroom))             ; Raum des Schalters (Quellraum)
  (Device {room == ?srcroom} (name ?dev_in_srcroom))  ; alle Geräte im Quellraum
  (or (Light {label == ?dev_in_srcroom && state > 0}) (DimLight {label == ?dev_in_srcroom && state > 0}))                                                ; im Quellraum brennt ein beliebiges Licht

  (Device {name == ?dest && room != ?srcroom} (room ?neighborroom)) ; ein Raum der Tür ist Quellraum, wir nehmen hier die/den anderen
  
  (Device {room == ?neighborroom} (name ?dev_in_neighborroom))     ; Geräte im Nachbarraum
  ?light <- (or (Light {type == "main" && label == ?dev_in_neighborroom}) (DimLight {type == "main" && label == ?dev_in_neighborroom}))                   ; Deckenlichter im Nachbarraum
  =>
    (add (new ChangeDevice ?light.label 255))        ; Deckenlichter einschalten
    (printout t "Aufgabe 3.3" crlf))
*/
  
(defrule uebung03aufg34_rooms
    ""
  (Telegram {dest == "sunLight"} (dest ?dest) (source ?src))  ; Regel nur feuern, wenn sich Türen oder Sonnenlicht ändern
  
  ?sun <- (Light {label == "sunLight"})   ; Sonnenlicht
  
  ;?devices_in_daylight_rooms <- (Device {(room == "livingroom" || room == "kitchen" || room == "bedroom") && room == ?*last_room*}) ; Geräte in Räumen, die immer Tageslicht haben, wenn die Sonne scheint
    
  ?devices_in_daylight_rooms <- (Device {(room == "livingroom" || room == "kitchen" || room == "bedroom")}) ; Geräte in Räumen, die immer Tageslicht haben, wenn die Sonne scheint
   
  ?lights_in_daylight_rooms <- (or (Light {type == "main" && label == devices_in_daylight_rooms.name}) (DimLight {type == "main" && label == devices_in_daylight_rooms.name})) ; Deckenlichter in diesen Räumen´
            
  =>
    (add (new ChangeDevice ?lights_in_daylight_rooms.label (mod (+ 1 (?sun.state intValue)) 2)))        ; Deckenlichter in Räumen ohne Tageslicht einschalten / in Räumen mit Tageslicht ausschalten
    (printout t "Aufgabe 3.4, WZ, SZ, Küche" crlf))
    
 
(defrule uebung03aufg34_corridor_door_open
    ""
  (Telegram {dest == "upperLeftDoor" || dest == "upperRightDoor"
             || dest == "lowerLeftDoor" || dest == "lowerRightDoor" || dest == "sunLight"})  ; Regel nur feuern, wenn sich Flurtüren oder Sonnenlicht sich ändern
    
  ?sun <- (Light {label == "sunLight"})   ; Sonnenlicht
  
  ;(test (= ?*last_room* "corridor"))
  
  (Door {label != "bathroomDoor" && state == 1})  ; mindestens eine Flurtür offen
  
  (Device {room == "corridor"} (name ?devices_in_corridor))            ; Geräte im Flur
  ?lights <- (Light{type == "main" && label == ?devices_in_corridor})   ; Deckenlicht im Flur

  =>
    (add (new ChangeDevice ?lights.label (mod (+ 1 (?sun.state intValue)) 2)))
    (printout t "Aufgabe 3.4, mindestens eine Flurtür offen" crlf))
    
(defrule uebung03aufg34_corridor_doors_closed
    ""
  (Telegram {dest == "upperLeftDoor" || dest == "upperRightDoor"
             || dest == "lowerLeftDoor" || dest == "lowerRightDoor"})   ; Regel nur feuern, wenn sich Flurtüren sich ändern
  
  ;(test (= ?*last_room* "corridor"))
  
  (not(Door {label != "bathroomDoor" && state == 1}))                   ; alle Flurtüren geschlossen
  
  (Device {room == "corridor"} (name ?devices_in_corridor))             ; Geräte im Flur
  ?lights <- (Light{type == "main" && label == ?devices_in_corridor})   ; Deckenlicht im Flur

  =>
    (add (new ChangeDevice ?lights.label 1)) ; Licht im Flur schalten
    (printout t "Aufgabe 3.4, keine Flurtür offen" crlf))
    
(defrule uebung03aufg34_bathroom_door_open
    ""
  (Telegram {dest == "upperLeftDoor" || dest == "upperRightDoor"
             || dest == "lowerLeftDoor" || dest == "lowerRightDoor" || dest == "bathroomdoor" || dest == "sunLight"})  ; Regel nur feuern, wenn sich Türen oder Sonnenlicht ändern
  
  ;(test (= ?*last_room* "bathroom"))
  
  ?sun <- (Light {label == "sunLight"})   ; Sonnenlicht
  
  (Door {label != "bathroomDoor" && state == 1})  ; mindestens eine Flurtür offen
  (Door {label == "bathroomDoor" && state == 1})  ; Badtür offen
  
  =>
    (add (new ChangeDevice "bathroomLight" (mod (+ 1 (?sun.state intValue)) 2)))
    (printout t "Aufgabe 3.4, mindestens eine Flurtür und die Badtür offen" crlf))
    
(defrule uebung03aufg34_bathroom_door_closed
    ""
  (Telegram {dest == "upperLeftDoor" || dest == "upperRightDoor"
             || dest == "lowerLeftDoor" || dest == "lowerRightDoor" || dest == "bathroomdoor"})  ; Regel nur feuern, wenn sich Türen ändern
      
  ;(test (= ?*last_room* "bathroom"))
      
  (or (not(Door {label != "bathroomDoor" && state == 1})) (Door {label == "bathroomDoor" && state == 0}))  ; alle Flurtüren geschlossen ODER Badtür geschlossen
  
  =>
    (add (new ChangeDevice "bathroomLight" 1))
    (printout t "Aufgabe 3.4, Flurtüren geschlossen oder Badtür geschlossen" crlf))
    
(defrule uebung03aufg34_switch
    "Variable auf den Raum des zuletzt gedrückten Schalters setzen"
  (Telegram (source ?src))
  
  (Switch {name == ?src} (room ?room))  ; Quelle ist ein Schalter
    
  =>
    (bind ?*last_room* ?room)
    (printout t "Aufgabe 3.4, letzter Raum geändert zu " ?*last_room* crlf))
    

/*
(defrule blablub
    ""
  (Telegram {dest == "upperLeftDoor" || dest == "upperRightDoor"
             || dest == "lowerLeftDoor" || dest == "lowerRightDoor" || dest == "bathroomdoor" || dest == "sunLight"})  ; Regel nur feuern, wenn sich Türen oder Sonnenlicht ändern
      
  ?sun <- (Light {label == "sunLight"})   ; Sonnenlicht
        
  (and (or (test ( =?*last_room* "livingroom")) (test (= ?*last_room* "kitchen")) (test (= ?*last_room* "bedroom"))) (test (eq sun.state 0)))
  
  ?devices <- (Device {(room == ?*last_room*})) ; Geräte im Raum, in dem zuletzt ein Schalter gedrückt wurde
    
  ?lights <- (or (Light {type == "main" && label == devices.name}) (DimLight {type == "main" && label == devices.name})) ; Deckenlichter in diesen Räumen
            
  =>
    (add (new ChangeDevice ?lights.label 1))        ; Deckenlichter einschalten
    (printout t "Aufgabe 3.4, single rule" crlf))
*/