require 'jsduck/tag_registry'

module JsDuck
  module Render

    # Performs the rendering of tags.
    class Tags
      # Renders tags of a particular section.
      #
      # Takes member or class hash.
      # Returns array of rendered HTML.
      def self.render(member)
        TagRegistry.html_renderers.map do |tag|
          if member[tag.tagname]
            tag.to_html(member)
          else
            nil
          end
        end
      end

      # Renders the signatures for a class member.
      # Returns a string.
      def self.render_signature(member)
        html = []
        TagRegistry.signatures.each do |s|
          if member[s[:tagname]]
            title = s[:tooltip] ? "title='#{s[:tooltip]}'" : ""
            html << "<span class='#{s[:tagname]}' #{title}>#{s[:long]}</span>"
          end
        end
        '<span class="signature">' + html.join + "</span>"
      end

    end

  end
end
