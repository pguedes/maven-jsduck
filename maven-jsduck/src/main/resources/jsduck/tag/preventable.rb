require "jsduck/tag/tag"

module JsDuck::Tag
  # That's pretty much a boolean tag, but we don't inherit from
  # BooleanTag as unlike other boolean tags it can be followed by some
  # text.
  class Preventable < Tag
    def initialize
      @pattern = "preventable"
      @tagname = :preventable
      @signature = {:long => "preventable", :short => "PREV"}
      @html_position = POS_PREVENTABLE
      # Use the default white box with dark gray border.
      @css = ".preventable-box { text-align: center }"
    end

    # @preventable is optionally followed by some method name, but we
    # don't document it.
    def parse_doc(p, pos)
      p.match(/.*$/)
      {:tagname => :preventable}
    end

    def process_doc(h, docs, pos)
      h[:preventable] = true
    end

    def to_html(context)
      <<-EOHTML
        <div class='rounded-box preventable-box'>
        <p>This action following this event is <b>preventable</b>.
        When any of the listeners returns false, the action is cancelled.</p>
        </div>
      EOHTML
    end
  end
end
