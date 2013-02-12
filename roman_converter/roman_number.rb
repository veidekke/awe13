class Roman_Number
  MAP = {
    1 => "I",
    4 => "IV",
    5 => "V",
    9 => "IX",
    10 => "X",
    40 => "XL",
    50 => "L",
    90 => "XC",
    100 => "C",
    400 => "CD",
    500 => "D",
    900 => "CM",
    1000 => "M"
    }

  def from_roman_number(roman)

  end

  def to_roman_number(latin)

  end

  # Returns false if roman is no valid roman number.
  # (Return to return immediately)
  def check_roman_syntax(roman)
    roman.upcase!
    roman.each_char { |c|
                        if !(MAP.has_value? c)
                            puts "#{c} ist kein gueltiges roemisches Literal."
                            return false
                        end
                    }

    if ((roman.include? "IIII") || (roman.include? "XXXX") || (roman.include? "CCCC") || (roman.include? "MMMM") || (roman.include? "VV") || (roman.include? "LL") || (roman.include? "DD"))
        puts "Ein Literal tritt zu oft hintereinander auf."
        return false
    end

    true
  end

  def check_latin_syntax(latin)
    !latin.match(/[^0-9]/) && latin.to_i >= 0
  end

end