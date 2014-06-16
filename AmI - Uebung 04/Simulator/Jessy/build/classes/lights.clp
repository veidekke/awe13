;; JESS Regeln für die 3. Übung
;; Deklaration aller Lichter und Türen im BAALL sowie einfache Regeln als Beispiel

(import jessy.*)
(deftemplate Light       (declare (from-class Light)))
(deftemplate DimLight       (declare (from-class DimLight)))
(deftemplate Door       (declare (from-class Door)))
(deftemplate ChangeDevice (declare (from-class ChangeDevice)))
(deftemplate PlaySound   (declare (from-class PlaySound)))
(deftemplate Telegram    (declare (from-class Telegram)))

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
(add (new Door "bathroomdoor" "3/2/0"));


;; Now define the rules.

; Beispiel für eine einfache Regel
(defrule badezimmertuer-auf-macht-badezimmerlicht-an 
    "Wird die Badezimmertuer geoeffnet, gehen die Lichter im Bad an"
    ?tele <- (Telegram { dest == "bathroomDoor" && value == 1 } )
    =>
    (add (new ChangeDevice "bathroomLight" 1))
    (add (new ChangeDevice "bathroomMirrorLight" 1))
    (add (new PlaySound "computerbeep_6.wav"))
	(printout t "Licht im Bad wurde eingeschaltet" crlf))

(defrule schlafzimmertuer-schaltet-flurlicht 
    "Wird im Schlafz eine Tuer geoeffnet, geht das flurflicht an falls im baall ein licht brennt."
    ?tele <- (Telegram {(source == "lowerRight" || source == "upperRight") &&
                (dest == "lowerRightDoor" || dest == "upperRightDoor") &&
		value == 1 } )
    (Light {state > 0} (label ?label))
    =>
    (add (new ChangeDevice "corridorLight" 1))
    (add (new PlaySound "computerbeep_6.wav")))
	
; Simulation eines Aussenlichtsensors, realisiert durch "sunLight"
(defrule sunlight-on
    "Wenn es hell wird, mach alle Deckenlichter aus."
    ?tele <- (Telegram { dest == "sunLight" && value == 1 } )
    ?light <- (or (Light {state > 0 && type == "main"}) (DimLight {state > 0 && type == "main"}))
    =>
    (add (new ChangeDevice ?light.label 0)))

(defrule sunlight-off
    "Wenn es dunkel wird, mach alle Deckenlichter an."
    ?tele <- (Telegram { dest == "sunLight" && value == 0 } )
    ?light <- (or (Light {state == 0 && type == "main"}) (DimLight {state == 0 && type == "main"}))
    =>
    (add (new ChangeDevice ?light.label 255)))





