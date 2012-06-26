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
options.processes = 0
options.template_dir = "target/jsduck_template"

js_files = []
# scan directory for .js files
  for d in input_path
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
