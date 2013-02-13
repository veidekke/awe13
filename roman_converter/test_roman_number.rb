require './roman_number'
require "test/unit"

class Roman_number_test < Test::Unit::TestCase
  def setup
    @roman_number = Roman_Number.new
  end
  
  #Testen von to_roman_number
  def test_simple_latin_numbers
    assert_equal("I", @roman_number.to_roman_number(1))
    assert_equal("V", @roman_number.to_roman_number(5))
    assert_equal("X", @roman_number.to_roman_number(10))
    assert_equal("L", @roman_number.to_roman_number(50))
    assert_equal("C", @roman_number.to_roman_number(100))
    assert_equal("D", @roman_number.to_roman_number(500))
    assert_equal("M", @roman_number.to_roman_number(1000))
  end
  
  def test_complecate_latin_numbers_without_subtraction
    assert_equal("II", @roman_number.to_roman_number(2))
    assert_equal("VI", @roman_number.to_roman_number(6))
    assert_equal("XVI", @roman_number.to_roman_number(16))
    assert_equal("DCL", @roman_number.to_roman_number(650))
    assert_equal("MMDCLXI", @roman_number.to_roman_number(2661))
  end
  
  def test_complecate_latin_numbers_with_subtraction
    assert_equal("IV", @roman_number.to_roman_number(4))
    assert_equal("MCMLI", @roman_number.to_roman_number(1951))
    assert_equal("CDXXXIV", @roman_number.to_roman_number(434))
  end
  
  def test_special_cases_latin_numbers
    assert_equal(nil, @roman_number.to_roman_number(-4), "Negative Zahlen sind nicht darstellbar")
  end
  
  #Testen von from_roman_number
  def test_simple_roman_numbers
    assert_equal(1, @roman_number.from_roman_number("I"))
    assert_equal(5, @roman_number.from_roman_number("V"))
    assert_equal(10, @roman_number.from_roman_number("X"))
    assert_equal(50, @roman_number.from_roman_number("L"))
    assert_equal(100, @roman_number.from_roman_number("C"))
    assert_equal(500, @roman_number.from_roman_number("D"))
    assert_equal(1000, @roman_number.from_roman_number("M"))
  end
  
  def test_complecate_roman_numbers_without_subtraction
    assert_equal("II", @roman_number.from_roman_number(2))
    assert_equal("VI", @roman_number.from_roman_number(6))
    assert_equal("XVI", @roman_number.from_roman_number(16))
    assert_equal("DCL", @roman_number.from_roman_number(650))
    assert_equal("MMDCLXI", @roman_number.from_roman_number(2661))
  end
  
  def test_complecate_roman_numbers_with_subtraction
    assert_equal("IV", @roman_number.from_roman_number(4))
    assert_equal("MCMLI", @roman_number.from_roman_number(1951))
    assert_equal("CDXXXIV", @roman_number.from_roman_number(434))
    
  end
  
  def test_special_cases_roman_numbers
    assert_equal(nil, @roman_number.from_roman_number("IIII"))
    assert_equal(nil, @roman_number.from_roman_number("IIV"))
  end
  
end