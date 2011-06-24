# fake RDiscount API to satisfy RDiscount dependency of jsduck.
# RDiscount uses C code by loading a .so file, which won't work in jruby and is not portable.
# replaces RDiscount with a pure ruby Markdown formatter.
class RDiscount
  def to_html()
    @maruku.to_html_document
  end

  def initialize(text)
    require 'maruku'
    @text  = text
    @maruku = Maruku.new(@text)
  end

end
