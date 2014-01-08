# Change the File.read method to ignore trying to read the non-existing VERSION file for the parallel gem.
# It hardcodes VERSION to be the content of the ../VERSION file, which we don't want to provide.
class File
  class << self
    alias_method :oldRead, :read

    def read(fileName)
      if (fileName.end_with?("VERSION"))
        return "1.0"
      else
        return oldRead(fileName)
      end
    end
  end
end

require 'jsduck/app'
require 'jsduck/options'

options = JsDuck::Options.new
options.output_dir = output_path
options.template_dir = "target/jsduck_template"
options.guides = guides 
if File.exists?(welcome_path)
  options.welcome = welcome_path
end
options.title = title
options.header = header
options.eg_iframe = eg_iframe
if css && css != ""
  options.css = css
end
if footer && footer != ""
  options.footer = footer
end

input_path_array = input_path.to_a

if(builtin_classes)
	input_path_array << "target/js-classes"
end

js_files = []
# scan directory for .js files
  for d in input_path_array
    if File.exists?(d)
      if File.directory?(d)
        Dir[d+"/**/*.{js,css,scss}"].each {|f| js_files << f }
      else
        js_files << d
      end
    else
      puts "Warning: File #{d} not found"
    end
  end  
options.input_files = js_files

app = JsDuck::App.new(options)
app.run()
