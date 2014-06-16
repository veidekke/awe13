;; First define templates for the model classes so we can use them
;; in our pricing rules. This doesn't create any model objects --
;; it just tells Jess to examine the classes and set up templates
;; using their properties

(import jessy.*)
(deftemplate Light       (declare (from-class Light)))
(deftemplate ChangeLight (declare (from-class ChangeLight)))
(deftemplate PlaySound   (declare (from-class PlaySound)))
(deftemplate Telegram    (declare (from-class Telegram)))

;; Now define the pricing rules themselves. Each rule matches a set
;; of conditions and then creates an Offer object to represent a
;; bonus of some kind given to a customer. The rules assume that
;; there will be just one Order, its OrderItems, and its Customer in 
;; working memory, along with all the CatalogItems.

(defrule schalterimschlafzimmer 
    "Wird im Schlafzimmer ein Schalter gedrueckt, schalte wohnzimmerlichter aus"
    ?tele <- (Telegram {source == "lowerRightSwitch" || source == "upperRightSwitch" } (value ?value))
    =>
    (add (new ChangeLight "livingLight1" 0))
    (add (new ChangeLight "livingLight2" 0))
    (add (new ChangeLight "livingLight3" 0))
    (add (new ChangeLight "livingJack1" 0))
    (add (new PlaySound "computerbeep_6.wav"))
)

(defrule schalterimwohnzimmer 
    "Wird im Wohnz ein Schalter gedrueckt, schalte schlafzimmerlichter aus"
    ?tele <- (Telegram {source == "lowerLeftSwitch" || source == "upperLeftSwitch"} (value ?value))
    =>
    (add (new ChangeLight "bedroomLight1" 0))
    (add (new ChangeLight "bedroomLight2" 0))
    (add (new ChangeLight "bedroomJack1" 0))
    (add (new ChangeLight "bedroomJack2" 0))
    (add (new PlaySound "computerbeep_6.wav"))
)

(defrule wohnzimmer-schaltet-tuer 
    "Wird im Wohnz eine Tuer geoeffnet, geht das flurflicht an falls im baall ein licht brennt."
    ?tele <- (Telegram {(source == "lowerLeftSwitch" || source == "upperLeftSwitch") &&
                (dest == "lowerLeftDoor" || dest == "upperLeftDoor") &&
		value == 1 } )
    (Light {state > 0} (label ?label))
    =>
    (add (new ChangeLight "corridorLight" 1))
    (add (new PlaySound "computerbeep_6.wav"))
)

(defrule schlafzimmer-schaltet-tuer 
    "Wird im Schlafz eine Tuer geoeffnet, geht das flurflicht an falls im baall ein licht brennt."
    ?tele <- (Telegram {(source == "lowerRightSwitch" || source == "upperRightSwitch") &&
                (dest == "lowerRightDoor" || dest == "upperRightDoor") &&
		value == 1 } )
    (Light {state > 0} (label ?label))
    =>
    (add (new ChangeLight "corridorLight" 1))
    (add (new PlaySound "computerbeep_6.wav"))
)
(defrule badezimmertuer-auf-macht-badezimmerlicht-an 
    "Wird im Wohnz eine Tuer geoeffnet, geht das flurflicht an falls im baall ein licht brennt."
    ?tele <- (Telegram { dest == "bathroomDoor" && value == 1 } )
    =>
    (add (new ChangeLight "bathroomLight" 1))
    (add (new ChangeLight "bathroomMirrorLight" 1))
    (add (new PlaySound "computerbeep_6.wav"))
)

