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
require 'optparse'

app = JsDuck::App.new

app.output_dir = output_path
app.verbose = verbose
app.processes = 0
app.template_dir = "target/jsduck_template"

js_files = []
# scan directory for .js files
  if File.exists?(input_path)
    if File.directory?(input_path)
      Dir[input_path+"/**/*.{js,css,scss}"].each {|f| js_files << f }
    else
      js_files << input_path
    end
  else
    puts "Warning: File #{input_path} not found"
  end
app.input_files = js_files

app.run()
