require "jsduck/meta_tag"

module JsDuck::Tag
  # Implementation of @deprecated tag
  class Deprecated < JsDuck::MetaTag
    def initialize
      @name = "deprecated"
      @key = :deprecated
      @signature = {:long => "deprecated", :short => "DEP"}
      @multiline = true
    end

    def to_value(contents)
      text = contents[0]
      if text =~ /\A([0-9.]+)(.*)\Z/
        {:version => $1, :text => $2.strip}
      else
        {:text => text || ""}
      end
    end

    def to_html(depr)
      v = depr[:version] ? "since " + depr[:version] : ""
      <<-EOHTML
        <div class='signature-box deprecated'>
        <p>This #{@context[:tagname]} has been <strong>deprecated</strong> #{v}</p>
        #{format(depr[:text])}
        </div>
      EOHTML
    end
  end
end

