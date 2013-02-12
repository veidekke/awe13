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
    
  end
  
  
  def test_complecate_latin_numbers_with_subtraction
    
  end
  
  def test_special_cases_latin_numbers
    
  end
  
  #Testen von from_roman_number
  def test_simple_roman_numbers
  
  end
  
  def test_complecate_roman_numbers_without_subtraction
  
  end
  
  def test_complecate_roman_numbers_with_subtraction
    
  end
  
  def test_special_cases_roman_numbers
    
  end
  
end