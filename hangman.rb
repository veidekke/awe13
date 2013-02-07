class Hangman

    # Set up stuff
    def initialize(word)
        @secret = word.downcase!
        @guessed_characters = []
        @tries_left = 6
        @guessed = "_" * word.length
    end

    # Has the player guessed the secret word?
    def won?
        @guessed == @secret
    end

    # Checks if player has guessed the whole word
    def guess_word(word)
        word == @secret
    end

    # Returns true if player's guessed character is in the secret word
    # Adds character to list of already guessed characters.
    # Also updates @guessed to reflect the current progress.
    def guess_character(char)
        char.downcase!

        if guess_word(char)
            @guessed = @secret
            return
        end

        if @guessed_characters.include? char
            puts "You have already guessed this character."
            return
        end

        @guessed_characters.push char

        if @secret.include? char
            counter = 0

            # Replace the '_' in @guessed with the characters where it occurs in the secret word
            @secret.each_char {|c|
                                if c == char
                                    @guessed[counter] = char
                                end

                                counter += 1
                            }

            puts "You've guessed correctly!"

        else
            @tries_left -= 1
            puts "Wrong..."
        end

    end

    def questioning
        while !won? && @tries_left > 0
            puts "Progress: #{@guessed}"
            puts "Tries left: #{@tries_left}"
            print "Guess a character (or type the whole word if you think you're really that cool): "
            x = gets.chomp
            guess_character(x)
        end

        if won?
            puts "Yay, you win! (And you even had #{@tries_left} tries left)"
        else
            puts "Duh, you loose :("
        end
    end

end

# Start the game loop
words = ["Hund", "Katze", "Maus", "Zirkus", "AWE", "Ruby", "Informatik", "Tisch", "Stuhl", "Maus", "Bett"]
hang = Hangman.new(words[rand(words.length)])
hang.questioning