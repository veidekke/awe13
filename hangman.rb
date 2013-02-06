class Hangman
	# TODO attr only for variables that really need it
	attr :secret
	attr :guessed_characters
	attr :tries_left
	attr :guessed
	
	# Set up stuff
	def initialize(word)
		@secret = word.downcase!
		@guessed_characters = []
		@tries_left = 6
		@guessed = "_" * word.length
	end
		
	# Has the player guessed the secret word?
	def won?
		guessed == secret
	end
	
	# Checks if player has guessed the whole word
	def guess_word(word)
		# TODO guess the whole word
	end
	
	# Returns true if player's guessed character is in the secret word
	# Adds character to list of already guessed characters.
	# Also updates @guessed to reflect the current progress.
	def guess_character(char)
		char.downcase!
	
		if guessed_characters.include? char
			false
			puts "You have already guessed this character."
			# TODO Exception?
		end
		guessed_characters.push char
		
		if @secret.include? char			
			counter = 0
			
			# Replace the '_' in @guessed with the characters where it occurs in the secret word 
			secret.each_char {|c|
					if c == char
						guessed[counter] = char
					end
					
					counter += 1
					}
						
			true
		else
			@tries_left -= 1
			false
		end
		
	end
	
end


# TODO Loop for user interaction until word is guess or tries is zero

hang = Hangman.new("Hund")
# TODO Random word? Word from command line argument? Word scraped from online word list?

puts "Progress: #{hang.guessed}"

if hang.guess_character("U")
	puts "correct guess"
else
	puts "incorrect guess"
end

puts "Tries left: #{hang.tries_left}"

# Test: guess again
hang.guess_character("u")

puts "Progress: #{hang.guessed}"


hang.guess_character("h")
puts "Tries left: #{hang.tries_left}"
puts "Progress: #{hang.guessed}"



