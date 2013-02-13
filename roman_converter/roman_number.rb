class Roman_Number
  MAP = {
    1000 => "M",
    900 => "CM",
    500 => "D",
    400 => "CD",
    100 => "C",
    90 => "XC",
    50 => "L",
    40 => "XL",
    10 => "X",
    9 => "IX",
    5 => "V",
    4 => "IV",
    1 => "I"
  }

  def from_roman_number(roman)
    return  if !check_roman_syntax roman

    latin = 0
    MAP.each { |k, v|
        # Little ugly that v and k are used the other way round
        until roman.index(v) != 0
            roman.slice! v
            latin += k
        end
    }
    
    latin
  end

  def to_roman_number(latin)
    return  if latin <= 0
    
    roman = ""
    MAP.each { |k, v|
                while latin >= k
                    roman += v
                    latin -= k
                end
            }
                    
    roman
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

end

rn = Roman_Number.new
puts rn.to_roman_number 650