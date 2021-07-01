# Documentation

## Dependencies

In an apt-based Linux distro, type the below commands to install the dependencies.

```bash
sudo apt-get install python3-sphinx
sudo pip install javasphinx
```

## Generate Documentation

In order to generate documentation, type the below commands:

```bash
javasphinx-apidoc -o . .. -f
make html
```

You will find the documentation in the `_build/html` directory.

If you want to clear the documentation, type:

```bash
make clean
```
